package com.talesajs.lockscreenjapan.data;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
public class RespWordData {
    int index;
    String level;
    String word;
    String kanji = "";
    String meaning;
}
