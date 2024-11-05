package com.kyawgyi.role.repo;

import com.kyawgyi.role.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepo extends JpaRepository<Movie, Integer> {
}
