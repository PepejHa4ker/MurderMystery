
package com.pepej.murdermystery.commands.completion;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;


@AllArgsConstructor
@Value
public class CompletableArgument {

  String mainCommand;
  String argument;
  List<String> completions;

}