package com.tycoon.community.tycoon;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tycoon.community.board.BoardMapper;
import com.tycoon.community.board.Comment;
import com.tycoon.community.board.CommentMapper;
import com.tycoon.community.board.Board;
import com.tycoon.community.common.GameException;

import lombok.RequiredArgsConstructor;
import com.tycoon.community.user.UserMapper;
import com.tycoon.community.user.ProfileImageStorage;
import com.tycoon.community.user.ProfileImageStorageMapper;

/**
 * 골드·DAY·모듈 구매와 NPC 저장 규칙을 처리합니다. DB 작업은 TycoonMapper 등에 위임합니다.
 */

@Service
@RequiredArgsConstructor
public class TycoonServiceImpl implements TycoonService {
	private final TycoonMapper tycoonMapper;
	private final BoardMapper boardMapper;
	private final CommentMapper commentMapper;
	private final UserMapper memberMapper;
	private final ProfileImageStorageMapper profileImageStorageMapper;

	@Override
	public GameData get(long userId) {
		GameData site = tycoonMapper.findByUser(userId);
		if (site == null) {
			throw new GameException(404, "사이트를 찾을 수 없습니다.");
		}
		if (site.getUserProfileImageId() != null) {
			ProfileImageStorage image = profileImageStorageMapper.find(site.getUserProfileImageId(), userId);
			if (image != null) site.setUserProfileImageUploadId(image.getUploadId());
		}
		return site;
	}

	@Override
	public GameData requireBoard(long userId) {
		GameData site = get(userId);
		if (!site.getHasBoard()) {
			throw new GameException(403, "게시판을 먼저 구매해 주세요.");
		}
		return site;
	}

	@Override
	public GameData requireComments(long userId) {
		GameData site = requireBoard(userId);
		if (!site.getHasComments()) {
			throw new GameException(403, "댓글을 먼저 구매해 주세요.");
		}
		return site;
	}

	@Transactional
	@Override
	public long save(long userId, GameSaveDTO data) {
		GameData site = get(userId);
		validate(data);
		checkModules(site, data);
		apply(site, data);
		tycoonMapper.update(site);
		return 0;
	}

	private void validate(GameSaveDTO data) {
		if (data.getGold() < 0 || data.getGold() > 9000000000000L
				|| data.getTraffic() < 0 || data.getTraffic() > 1000000
				|| data.getDay() < 1 || data.getDay() > 1000000
				|| data.getPlaySeconds() < 0 || data.getPlaySeconds() >= 600) {
			throw new GameException("저장할 숫자의 범위를 확인해 주세요.");
		}
		if (data.isComment() && !data.isBoard()) {
			throw new GameException("게시판이 있어야 댓글을 사용할 수 있습니다.");
		}
	}

	private void apply(GameData site, GameSaveDTO data) {
		site.setGold(data.getGold());
		site.setTraffic(data.isBoard() ? data.getTraffic() : 0);
		site.setDay(data.getDay());
		site.setHasBoard(data.isBoard());
		site.setHasComments(data.isComment());
		site.setHasMypage(data.isMypage());
		site.setHasProfileImage(data.isProfileImage());
		site.setHasAttachment(data.isAttachment());
		site.setHasPaging(data.isPaging());
		site.setHasSearch(data.isSearch());
		site.setHasTheme(data.isTheme());
		site.setHasProfileImageStorage(data.isProfileImageStorage());
		site.setPlaySeconds(data.getPlaySeconds());
	}

	// NPC 글/댓글과 골드/트래픽을 같은 트랜잭션에서 함께 저장합니다.

	@Transactional
	@Override
	public long npc(long userId, GameSaveDTO data) {
		validate(data);
		GameData site = get(userId);
		checkModules(site, data);
		if (!data.isBoard()) {
			throw new GameException("게시판이 잠겨 있습니다.");
		}
		Board post = new Board();
		post.setGameDataId(site.getGameDataId());
		post.setAuthorType("NPC");
		post.setAuthorNickname(checkText(data.getAuthor(), 30));
		post.setTitle(checkText(data.getTitle(), 100));
		post.setContent(checkText(data.getContent(), 10000));
		post.setCreatedDay(data.getDay());
		boardMapper.insert(post);

		if (data.isComment() && data.getReply() != null) {
			Comment comment = new Comment();
			comment.setBoardId(post.getBoardId());
			comment.setAuthorNickname(checkText(data.getReplyAuthor(), 30));
			comment.setContent(checkText(data.getReply(), 1000));
			comment.setAuthorType("NPC");
			commentMapper.insert(comment);
            if (data.getReplyBack() != null && "forest".equals(site.getUserTheme())) {
                Comment answer = new Comment();
                answer.setBoardId(post.getBoardId());
                answer.setAuthorType("NPC");
                answer.setAuthorNickname(post.getAuthorNickname());
                answer.setContent(checkText(data.getReplyBack(), 1000));
                commentMapper.insert(answer);
            }
		}
		// 사용자 글은 건드리지 않고 이 사이트의 오래된 NPC 글·댓글만 정리합니다.
		boardMapper.deleteOldNpcComments(site.getGameDataId());
		boardMapper.deleteOldNpcPosts(site.getGameDataId());
		save(userId, data);
		return post.getBoardId();
	}

	private String checkText(String value, int maxLength) {
		if (value == null || value.trim().isEmpty() || value.length() > maxLength) {
			throw new GameException("글자 수를 확인해 주세요.");
		}
		return value.trim();
	}

	private void checkModules(GameData site, GameSaveDTO data) {
		if (site.getHasBoard() != data.isBoard() || site.getHasComments() != data.isComment()
				|| site.getHasMypage() != data.isMypage() || site.getHasProfileImage() != data.isProfileImage()
				|| site.getHasAttachment() != data.isAttachment() || site.getHasPaging() != data.isPaging()
				|| site.getHasSearch() != data.isSearch() || site.getHasTheme() != data.isTheme() || site.getHasProfileImageStorage() != data.isProfileImageStorage()) {
			throw new GameException(403, "모듈은 기능 구매에서 해금해 주세요.");
		}
	}

	@Override
	public void requireModule(long userId, String module) {
		GameData site = get(userId);
		boolean unlocked = false;
		switch (module) {
			case "mypage":
				unlocked = site.getHasMypage();
				break;
			case "profileImage":
				unlocked = site.getHasMypage() && site.getHasProfileImage();
				break;
			case "attachment":
				unlocked = site.getHasBoard() && site.getHasAttachment();
				break;
			case "profileImageStorage":
				unlocked = site.getHasMypage() && site.getHasProfileImage() && site.getHasProfileImageStorage();
				break;
			case "theme":
				unlocked = site.getHasTheme();
				break;
			default: break;
		}
		if (!unlocked) throw new GameException(403, "기능 구매에서 해당 모듈을 먼저 해금해 주세요.");
	}

	@Transactional
	@Override
	public GameData action(long userId, GameSaveDTO data, String action, String module) {
		checkModules(get(userId), data);
		validate(data);
		if ("buy".equals(action)) {
			Integer price = ModuleShop.prices().get(module);
			if (price == null) throw new GameException("존재하지 않는 모듈입니다.");
			boolean already = isModuleEnabled(data, module);
			boolean admin = "ADMIN".equals(memberMapper.findByUserId(userId).getRole());

			// 이 시연용 전환은 골드를 환불하지 않으며, 일반 사용자는 구매한 모듈을 끌 수 없습니다.
			if (already) {
				if (!admin) throw new GameException("이미 구매한 기능입니다.");
				setModuleEnabled(data, module, false);
				if ("board".equals(module)) {
					data.setComment(false);
					data.setAttachment(false);
					data.setPaging(false);
					data.setSearch(false);
				} else if ("mypage".equals(module)) {
					data.setProfileImage(false);
					data.setProfileImageStorage(false);
				} else if ("profileImage".equals(module)) {
					data.setProfileImageStorage(false);
				}
			} else {
				if (data.getGold() < price) throw new GameException("골드가 부족합니다.");
			if (("comment".equals(module) || "attachment".equals(module) || "paging".equals(module)
					|| "search".equals(module)) && !data.isBoard()) {
				throw new GameException("게시판을 먼저 구매해 주세요.");
			}
			if ("profileImage".equals(module) && !data.isMypage()) throw new GameException("마이페이지를 먼저 구매해 주세요.");
			if ("profileImageStorage".equals(module) && (!data.isMypage() || !data.isProfileImage())) {
				throw new GameException("프로필 사진 모듈을 먼저 구매해 주세요.");
			}
				setModuleEnabled(data, module, true);
				data.setGold(data.getGold() - price);
			}
			GameData updated = get(userId);
			apply(updated, data);
			tycoonMapper.update(updated);
			return updated;
		} else if ("feed".equals(action)) {
			if (data.getGold() <= 10) throw new GameException("사료를 주려면 10 G보다 많아야 해요.");
			data.setGold(data.getGold() - 10);
		} else if ("bonus".equals(action)) {
			if (!"ADMIN".equals(memberMapper.findByUserId(userId).getRole())) {
				throw new GameException(403, "관리자 계정만 사용할 수 있습니다.");
			}
			data.setGold(Math.min(9000000000000L, data.getGold() + 1000));
		} else if ("tamper".equals(action)) {
			data.setGold(data.getGold() % 10);
		} else {
			throw new GameException("알 수 없는 게임 행동입니다.");
		}
		GameData updated = get(userId);
		apply(updated, data);
		tycoonMapper.update(updated);
		return updated;
	}

	private boolean isModuleEnabled(GameSaveDTO data, String module) {
		switch (module) {
			case "board": return data.isBoard();
			case "comment": return data.isComment();
			case "mypage": return data.isMypage();
			case "profileImage": return data.isProfileImage();
			case "attachment": return data.isAttachment();
			case "paging": return data.isPaging();
			case "search": return data.isSearch();
			case "theme": return data.isTheme();
			case "profileImageStorage": return data.isProfileImageStorage();
			default: throw new GameException("존재하지 않는 모듈입니다.");
		}
	}

	private void setModuleEnabled(GameSaveDTO data, String module, boolean enabled) {
		switch (module) {
			case "board": data.setBoard(enabled); break;
			case "comment": data.setComment(enabled); break;
			case "mypage": data.setMypage(enabled); break;
			case "profileImage": data.setProfileImage(enabled); break;
			case "attachment": data.setAttachment(enabled); break;
			case "paging": data.setPaging(enabled); break;
			case "search": data.setSearch(enabled); break;
			case "theme": data.setTheme(enabled); break;
			case "profileImageStorage": data.setProfileImageStorage(enabled); break;
			default: throw new GameException("존재하지 않는 모듈입니다.");
		}
	}
}
