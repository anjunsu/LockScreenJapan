package com.talesajs.lockscreenjapan.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class LevelData {
    String name;
    boolean isUsed;
}
