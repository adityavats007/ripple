package in.ripple.user.persistence.dao;

import in.ripple.user.persistence.entity.UserEntity;
import in.ripple.user.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDaoService {

    @Autowired
    UserRepository userRepository;


    public <S extends UserEntity> S save(S entity) {
        return userRepository.save(entity);
    }


    public Iterable findAll() {
        return userRepository.findAll();
    }

    public UserEntity findByEmail(String email) {
        return userRepository.getUSerByEmail(email);
    }
    public UserEntity findByMobile(String mobile) {
        return userRepository.getUserByMobile(mobile);
    }

    public UserEntity findByUserName(String userName) {
        return userRepository.getUserByUserName(userName);
    }

    public UserEntity findById(long id) {
        List<Long> idList=new ArrayList<>();
        idList.add(id);
        return userRepository.findAllById(idList).size()>0?userRepository.findAllById(idList).get(0):null;
    }

}
