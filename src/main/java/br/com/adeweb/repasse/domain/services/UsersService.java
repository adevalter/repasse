package br.com.adeweb.repasse.domain.services;


import br.com.adeweb.repasse.data.models.UsersDTO;
import br.com.adeweb.repasse.domain.entities.User;
import br.com.adeweb.repasse.domain.repositories.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<UsersDTO> findAll(Pageable pageable){
        Page<User> users = userRepository.findAll(pageable);
        return users.map((user -> convertToDTO(user)));
    }
    public UsersDTO buscaPorId(Long id){
        User user = userRepository.findById(id).orElseThrow(EntityExistsException::new);
        return convertToDTO(user);
    }

    public UsersDTO salvar(UsersDTO usersDTO){
        User user = convertToUsers(usersDTO);

        user.setStatus(1);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

        userRepository.save(user);
        return convertToDTO(user);
    }

    public UsersDTO atualizaUsers(Long id, UsersDTO usersDTO){
        User user = convertToUsers(usersDTO);
        user.setId(id);
        user = userRepository.save(user);
        return convertToDTO(user);
    }


    private UsersDTO convertToDTO(User user){
        return modelMapper.map(user, UsersDTO.class);
    }

    private User convertToUsers(UsersDTO usersDTO){
        return modelMapper.map(usersDTO, User.class);
    }
}
