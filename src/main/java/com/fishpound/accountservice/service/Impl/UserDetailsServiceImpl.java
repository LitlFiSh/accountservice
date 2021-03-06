package com.fishpound.accountservice.service.Impl;

import com.fishpound.accountservice.entity.Account;
import com.fishpound.accountservice.entity.Role;
import com.fishpound.accountservice.entity.UserInfo;
import com.fishpound.accountservice.repository.AccountRepository;
import com.fishpound.accountservice.repository.UserInfoRepository;
import com.fishpound.accountservice.security.jwt.JWTTokenUtils;
import com.fishpound.accountservice.security.jwt.JWTUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserInfoRepository userInfoRepository;
    @Autowired
    AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo user = userInfoRepository.getById(username);
        if(user == null){
            throw new UsernameNotFoundException("找不到该用户");
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        Account account = user.getAccount();
        if(user != null) {
            Role role = account.getRole();
            GrantedAuthority authority = new SimpleGrantedAuthority(JWTTokenUtils.ROLE_CLAIMS + role.getRoleName());
            grantedAuthorities.add(authority);
        }
        String uid = user.getId();
        String name = user.getUsername();
        String password = account.getPassword();
        Integer rid = account.getRole().getId();
        String deptName = user.getDepartment().getDeptName();
        boolean isActive = account.isActive();
        return new JWTUser(uid, name, password, rid, deptName, isActive, grantedAuthorities);
    }
}
