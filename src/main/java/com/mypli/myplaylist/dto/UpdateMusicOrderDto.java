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
    private Long[] musicIds;

    @Builder
    public UpdateMusicOrderDto(Long[] musicIds) {
        this.musicIds = musicIds;
    }
}
