package sumcoda.webide.member.auth.social;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo{

    // 응답이 json 형태로 전송되기 때문에 map 으로 받아야한다.
    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        // 구글은 id 값을 "sub"이라고 되어있다.
        return attributes.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getUsername() {
        return getProvider() + " " + getProviderId();
    }
}
