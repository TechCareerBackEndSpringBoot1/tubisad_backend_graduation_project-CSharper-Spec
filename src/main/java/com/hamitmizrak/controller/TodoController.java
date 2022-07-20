package com.hamitmizrak.controller;

import com.hamitmizrak.dto.TodoDto;
import com.hamitmizrak.data.entity.TodoEntity;
import com.hamitmizrak.data.repository.ITodoRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@Log4j2
public class TodoController {

    final
    ITodoRepository repository;

    public TodoController(ITodoRepository repository) {
        this.repository = repository;
    }


    //Save-GET
    // http://localhost:8080/do/save
    @GetMapping("do/save")
    public String getSave(Model model) {
        model.addAttribute("todo_save", new TodoDto());
        return "todo_save";
    }

    //Save-POST
    // http://localhost:8080/do/save
    @PostMapping("do/save")
    public String postSave(@Valid @ModelAttribute("todo_save") TodoDto toDoListDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Bir hata meydana geldi");
            return "todo_save";
        }
        TodoEntity toDoListEntity = new TodoEntity();
        toDoListEntity.setListMember(toDoListDto.getListMember());
        repository.save(toDoListEntity);
        return "redirect:/do/list";
    }

    //List
    //http://localhost:8080/do/list
    @GetMapping("do/list")
    public String todolist(Model model) {
        List<TodoEntity> list = repository.findAll();
        model.addAttribute("todo_list", list);
        return "todo_list";
    }

    //Ekleme-GET
    //http://localhost:8080/add/list
    @GetMapping("add/list")
    public String getAddToList(Model model) {
        model.addAttribute("todo_add", new TodoEntity());
        return "todo_add";
    }


    //Ekleme-POST
    //http://localhost:8080/add/list
    @PostMapping("add/list")
    public String addToList(@Valid @ModelAttribute("todo_add") TodoEntity todoEntity, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("Görev eklenemedi.");
        }
        repository.save(todoEntity);
        return "redirect:/do/list";
    }

    //Update-GET
    //http://localhost:8080/update/1
     @GetMapping("update/{id}")
     public String getUpdate(@PathVariable(name = "id")Long id, Model model){
        Optional<TodoEntity> optionalEntity = repository.findById(id);
        if(optionalEntity.isPresent()){
            model.addAttribute("update_key",optionalEntity.get());
        }else{
            model.addAttribute("update_key","başarısız");
        }
        return "todo_update";
     }

     //Update-POST
    //http://localhost:8080/update/1
    @PostMapping("update/{id}")
    public String postUpdate(@Valid @PathVariable(name = "id")Long id,@ModelAttribute("update_key")TodoDto dto,BindingResult result){
        if (result.hasErrors()){
            return "todo_update";
        }
        Optional<TodoEntity>optionalEntity = repository.findById(id);
        if (optionalEntity.isPresent()){
            TodoEntity entity = optionalEntity.get();
            entity.setListMember(dto.getListMember());
            repository.save(entity);
        }
        return "redirect:/do/list";
    }

    //Delete
    //http://localhost:8080/delete/1
    @GetMapping("delete/{id}")
    public String deleteById (@PathVariable(name = "id") Long id, Model model){
        Optional<TodoEntity> optionalEntity = repository.findById(id);
        if (optionalEntity.isPresent()) {
            model.addAttribute("delete_success", optionalEntity.get());
            repository.deleteById(id);
            log.info("Silme işlemi başarılı");
        } else {
            model.addAttribute("delete_failed", "Silme işlemi başarısız oldu.");
        }
        return "redirect:/do/list";
    }

}