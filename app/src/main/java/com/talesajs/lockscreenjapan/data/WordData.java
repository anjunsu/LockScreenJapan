package com.talesajs.lockscreenjapan.data;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
@Data
public class WordData {
    int index;
    String level;
    String word;
    String kanji = "";
    String meaning;
}
