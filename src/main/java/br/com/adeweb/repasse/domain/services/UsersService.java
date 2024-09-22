package br.com.adeweb.repasse.domain.services;


import br.com.adeweb.repasse.data.models.UsersDTO;
import br.com.adeweb.repasse.domain.entities.Users;
import br.com.adeweb.repasse.domain.enums.StatusUsers;
import br.com.adeweb.repasse.domain.repositories.UsersRepository;
import jakarta.persistence.EntityExistsException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<UsersDTO> findAll(Pageable pageable){
        Page<Users> users = usersRepository.findAll(pageable);
        return users.map((user -> convertToDTO(user)));
    }
    public UsersDTO buscaPorId(Long id){
        Users users = usersRepository.findById(id).orElseThrow(EntityExistsException::new);
        return convertToDTO(users);
    }

    public UsersDTO salvar(UsersDTO usersDTO){
        Users users = convertToUsers(usersDTO);
        users.setStatus(StatusUsers.confirmed);
        usersRepository.save(users);
        return convertToDTO(users);
    }

    public UsersDTO atualizaUsers(Long id, UsersDTO usersDTO){
        Users users = convertToUsers(usersDTO);
        users.setId(id);
        users = usersRepository.save(users);
        return convertToDTO(users);
    }


    private UsersDTO convertToDTO(Users users){
        return modelMapper.map(users, UsersDTO.class);
    }

    private Users convertToUsers(UsersDTO usersDTO){
        return modelMapper.map(usersDTO, Users.class);
    }
}
