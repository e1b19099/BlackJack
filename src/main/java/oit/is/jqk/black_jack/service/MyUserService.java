package oit.is.jqk.black_jack.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import oit.is.jqk.black_jack.model.MyUserDetails;
import oit.is.jqk.black_jack.model.Userinfo;
import oit.is.jqk.black_jack.model.UserinfoMapper;

@Service
public class MyUserService implements UserDetailsService {
  @Autowired
  private final UserinfoMapper uMapper;

  @Autowired
  public MyUserService(UserinfoMapper uMapper) {
    this.uMapper = uMapper;
  }

  public int insertUserData(Userinfo userinfo) {
    
    return 1;
  }

  @Override
  public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

    Optional<Userinfo> user = uMapper.findUser(userId);
    if (user == null) {
      throw new UsernameNotFoundException(userId + "が存在しません");
    }
    return new MyUserDetails(user.get());
  }
}
