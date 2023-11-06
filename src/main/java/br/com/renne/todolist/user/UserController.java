package br.com.renne.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private IUserRepository iUserRepository;
    public UserController(IUserRepository iUserRepository) {
        this.iUserRepository = iUserRepository;
    }

    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel){
        var user = this.iUserRepository.findByUsername(userModel.getUsername());
        if(user != null){
            // Mensagem de erro
            // Status code
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe");
        }
        // Faz a criptografia da senha
        var passwordHashered =  BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        // Atualiza a senha normal para um hash
        userModel.setPassword(passwordHashered);

        var userCreated = this.iUserRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }
}
