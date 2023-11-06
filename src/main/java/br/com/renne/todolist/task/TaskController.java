package br.com.renne.todolist.task;

import br.com.renne.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private ITaskRepositoty iTaskRepositoty;
    public TaskController(ITaskRepositoty iTaskRepositoty) {
        this.iTaskRepositoty = iTaskRepositoty;
    }

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        //System.out.println("Chegou no controller" + request.getAttribute("idUser"));
        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser); // tipo de usuario igual a UUID

        var currentDate = LocalDateTime.now();
        // 10/11/2023
        // 10/10/2023
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){  // Se a data for maior, se o currentDate for maior que taskModel.getStartAt
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início / data de termino deve ser maior do que a data atual");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt()) ){  // Se a data for maior, se o currentDate for maior que taskModel.getStartAt
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início deve ser menor que a data de término");
        }

        var task = this.iTaskRepositoty.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }


    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        var tasks = this.iTaskRepositoty.findByIdUser((UUID) idUser);
        return tasks;
    }

    // http://localhost:8080/tasks/88373774-7737872-3u72324
    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request){
        var task = this.iTaskRepositoty.findById(id).orElse(null);

        if(task == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada");
        }

        var idUser = request.getAttribute("idUser");
        if(!task.getIdUser().equals(idUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não tem permissão para alterar essa tarefa");
        }

        Utils.copyNonNullproperties(taskModel,task);
        var taskUpdated = this.iTaskRepositoty.save(task);
        return ResponseEntity.ok().body(taskUpdated);
    }



}


















