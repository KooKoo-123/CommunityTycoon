package com.tycoon.community.tycoon;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tycoon.community.common.GameException;
import com.tycoon.community.user.User;
import com.tycoon.community.user.UserMapper;
import lombok.RequiredArgsConstructor;

/** 관리자 자신의 사이트에서 모듈을 무료로 켜고 끕니다. 데이터는 삭제하지 않습니다. */

@Service
@RequiredArgsConstructor
public class AdminModuleService {
    private final UserMapper memberMapper;
    private final TycoonMapper tycoonMapper;

    @Transactional
    public GameData setEnabled(long userId, String module, boolean enabled) {
        User member = memberMapper.findByUserId(userId);
        if (member == null || !"ADMIN".equals(member.getRole())) {
            throw new GameException(403, "관리자 계정만 모듈을 전환할 수 있습니다.");
        }
        if (!ModuleShop.prices().containsKey(module)) throw new GameException("존재하지 않는 모듈입니다.");
        GameData site = tycoonMapper.findByUser(userId);
        if (site == null) throw new GameException(404, "사이트를 찾을 수 없습니다.");
        switch (module) {
            case "board": site.setHasBoard(enabled); break;
            case "comment": site.setHasComments(enabled); break;
            case "mypage": site.setHasMypage(enabled); break;
            case "profileImage": site.setHasProfileImage(enabled); break;
            case "attachment": site.setHasAttachment(enabled); break;
            case "paging": site.setHasPaging(enabled); break;
            case "search": site.setHasSearch(enabled); break;
            case "theme": site.setHasTheme(enabled); break;
            case "profileImageStorage": site.setHasProfileImageStorage(enabled); break;
            default: throw new GameException("존재하지 않는 모듈입니다.");
        }
        // 선행 모듈이 필요한 기능을 켜면 함께 켭니다. 부모를 끄면 하위 기능도 끕니다.
        if (enabled) {
            if ("comment".equals(module) || "attachment".equals(module) || "paging".equals(module) || "search".equals(module)) site.setHasBoard(true);
            if ("profileImage".equals(module) || "profileImageStorage".equals(module)) site.setHasMypage(true);
            if ("profileImageStorage".equals(module)) site.setHasProfileImage(true);
        } else {
            if ("board".equals(module)) {
                site.setHasComments(false); site.setHasAttachment(false);
                site.setHasPaging(false); site.setHasSearch(false); site.setTraffic(0);
            }
            if ("mypage".equals(module)) { site.setHasProfileImage(false); site.setHasProfileImageStorage(false); }
            if ("profileImage".equals(module)) site.setHasProfileImageStorage(false);
            if ("theme".equals(module)) {
                site.setUserTheme("sky");
                tycoonMapper.updateProfile(userId, site.getUserNickname(), "sky");
            }
        }
        tycoonMapper.update(site);
        return site;
    }
}
