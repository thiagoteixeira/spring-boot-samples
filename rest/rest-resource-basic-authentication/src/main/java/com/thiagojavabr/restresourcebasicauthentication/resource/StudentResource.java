package com.thiagojavabr.restresourcebasicauthentication.resource;

import com.thiagojavabr.restresourcebasicauthentication.domain.Student;
import com.thiagojavabr.restresourcebasicauthentication.repository.StudentRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/students")
public class StudentResource {

    @Autowired
    private StudentRepository repo;

    @GetMapping
    public Collection<Student> findAll(){
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> findById(@PathVariable Long id){
        Optional<Student> studentOpt = repo.findById(id);
        return studentOpt.isPresent() ? ResponseEntity.ok(studentOpt.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Student> create(@RequestBody Student student){
        Student studentSaved = repo.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentSaved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable Long id, @RequestBody Student student){
        Optional<Student> studentDb = repo.findById(id);
        if(!studentDb.isPresent()){
            return ResponseEntity.notFound().build();
        }
        Student studentToUpdate = studentDb.get();
        BeanUtils.copyProperties(student, studentToUpdate, "id");
        return ResponseEntity.ok(studentToUpdate);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        repo.deleteById(id);
    }
}
