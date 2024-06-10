package sumcoda.webide.member.auth.general;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sumcoda.webide.member.dto.MemberResponseDTO;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final MemberResponseDTO member;


    public CustomUserDetails(MemberResponseDTO member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        grantedAuthorities.add((GrantedAuthority) () -> member.getRole().getValue());

        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }

    // 사용자의 아이디가 만료되었는지
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 사용자의 아이디가 잠겨있는지
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 사용자의 아이디가 사용 가능한지
    @Override
    public boolean isCredentialsNonExpired() {
        return true;

    }

    // 사용자의 아이디가 사용 가능한지
    @Override
    public boolean isEnabled() {
        return true;
    }
}
