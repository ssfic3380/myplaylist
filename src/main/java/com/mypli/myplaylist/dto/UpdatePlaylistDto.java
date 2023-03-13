package com.mypli.myplaylist.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter
@NoArgsConstructor
public class UpdatePlaylistDto {

    @NotNull
    private Long playlistId;

    @NotNull @Size(max = 128)
    private String playlistName;

    @Builder
    public UpdatePlaylistDto(Long playlistId, String playlistName) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
    }
}
