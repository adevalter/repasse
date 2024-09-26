package br.com.adeweb.repasse.data.controllers;

import br.com.adeweb.repasse.data.models.UserDTO;
import br.com.adeweb.repasse.domain.entities.User;
import br.com.adeweb.repasse.domain.services.UsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersService usersService;
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(
        @RequestParam(defaultValue = "0") final Integer pageNumber,
        @RequestParam(defaultValue = "10") final Integer size
    ){
        return ResponseEntity.ok(usersService.findAll(PageRequest.of(pageNumber,size)));
    }

    @GetMapping("/{id}")
    public UserDTO buscaPorIdUsers(@PathVariable Long id){
        return usersService.buscaPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO addUsers(@RequestBody User user){
        return usersService.salvar(user);
    }

    /*@PutMapping("/{id}")
    public UserDTO updateUsers(@PathVariable Long id, @RequestBody User users){
        User usersAtual = usersService.buscaPorId(id);
        users.setCreatedAt(usersAtual.getCreatedAt());
        users.setUpdatedAt(usersAtual.getUpdatedAt());
        BeanUtils.copyProperties(users, usersAtual, "id");
        return usersService.salvar(usersAtual);
    }
*/
}
