

package pl.plajer.murdermystery.commands.arguments.data;

import lombok.Getter;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
public class LabeledCommandArgument extends CommandArgument {

  @Getter
  private final LabelData labelData;

  public LabeledCommandArgument(String argumentName, String permissions, ExecutorType validExecutors, LabelData labelData) {
    super(argumentName, permissions, validExecutors);
    this.labelData = labelData;
  }

}
