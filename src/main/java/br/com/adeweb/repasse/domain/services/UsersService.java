package br.com.adeweb.repasse.domain.services;


import br.com.adeweb.repasse.data.models.UserDTO;
import br.com.adeweb.repasse.domain.entities.User;
import br.com.adeweb.repasse.domain.repositories.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<UserDTO> findAll(Pageable pageable){
        Page<User> users = userRepository.findAll(pageable);
        return users.map((user -> convertToDTO(user)));
    }
    public UserDTO buscaPorId(Long id){
        User user = userRepository.findById(id).orElseThrow(EntityExistsException::new);
        return convertToDTO(user);
    }

    public UserDTO salvar(User user){

        user.setStatus(1);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return convertToDTO(user);
    }

    public UserDTO atualizaUsers(Long id, UserDTO userDTO){
        User user = convertToUsers(userDTO);
        user.setId(id);
        user = userRepository.save(user);
        return convertToDTO(user);
    }


    private UserDTO convertToDTO(User user){
        return modelMapper.map(user, UserDTO.class);
    }

    private User convertToUsers(UserDTO userDTO){
        return modelMapper.map(userDTO, User.class);
    }
}
