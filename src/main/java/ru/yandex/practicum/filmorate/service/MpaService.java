package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    @Autowired
    private final MpaRepository mpaRepository;

    public Collection<Mpa> getAllMpa() {
        return mpaRepository.getAllMpa();
    }

    public Mpa getMpaById(Integer mpaId) {
        return mpaRepository.getMpaById(mpaId);
    }
}