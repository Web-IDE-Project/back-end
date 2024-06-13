package sumcoda.webide.member.auth.social;

public interface OAuth2UserInfo {

    // 로그인 서비스 제공자 (Ex. naver, google, ...)
    String getProvider();

    // 서비스 제공자에서 발급해주는 아이디(번호)
    String getProviderId();

    // 이메일
    String getEmail();

    // 사용자 성명 (설정한 이름)
    String getName();

    // OAuth2로 전달받은 소셜로그인 데이터는 유저이름이라고 지칭할 수 있는 데이터가 없다. 그래서 소셜로그인 데이터 제공처와 제공처에서 전달해준 아이디 값을 바탕으로 유저이름을 리턴하는 메서드를 구현
    String getUsername();
}
