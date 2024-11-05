package com.kyawgyi.role.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {

    private int id;

    @NotBlank(message = "Please provide movie's title.")
    private String title;

    @NotBlank(message = "Please provide movie's director.")
    private String director;

    @NotBlank(message = "Please provide movie's studio.")
    private String studio;

    private Set<String> movieCast;

    private int releaseYear;

    @NotBlank(message = "Please provide movie's poster.")
    private String poster;

    @NotBlank(message = "Please provide posterUrl.")
    private String posterUrl;

}
