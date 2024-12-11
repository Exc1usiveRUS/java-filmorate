package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorRepository directorRepository;

    public List<Director> getAllDirectors() {
        return directorRepository.findAll();
    }

    public Director findDirectorById(Integer id) {
        return directorRepository.findById(id);
    }

    public Director createDirector(Director director) {
        return directorRepository.create(director);
    }

    public Director updateDirector(Director director) {
        Director updateDirector = directorRepository.findById(director.getId());
        if (director.getName() != null) updateDirector.setName(director.getName());
        return directorRepository.update(director);
    }

    public void deleteDirector(Integer id) {
        directorRepository.delete(id);
    }
}
