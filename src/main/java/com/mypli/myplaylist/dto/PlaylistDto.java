package com.mypli.myplaylist.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PlaylistDto {

    @NotNull
    private Long playlistId;

    @NotNull @Size(max = 128)
    private String playlistName;

    @Size(max = 255)
    private String playlistImg;

}
