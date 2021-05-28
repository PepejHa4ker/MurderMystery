
package com.pepej.murdermystery.locale;

import lombok.ToString;
import lombok.Value;

import java.util.List;


@Value
@ToString
public class Locale {
  String name;
  String originalName;
  String prefix;
  String author;
  List<String> aliases;

}
