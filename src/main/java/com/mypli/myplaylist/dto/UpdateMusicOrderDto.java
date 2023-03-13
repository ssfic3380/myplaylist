package com.mypli.myplaylist.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
@NoArgsConstructor
public class UpdateMusicOrderDto {

    @NotNull
    private Long playlistId;

    @NotNull
    private Long[] musicIds;

    @Builder
    public UpdateMusicOrderDto(Long playlistId, Long[] musicIds) {
        this.playlistId = playlistId;
        this.musicIds = musicIds;
    }
}
