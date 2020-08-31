
package pl.plajer.murdermystery.commands.arguments.data;

import lombok.Data;
import pl.plajer.murdermystery.handlers.ChatManager;

/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
@Data
public class LabelData {

  String text;
  String command;
  String description;

  public LabelData(String text, String command, String description) {
    this.text = ChatManager.colorRawMessage(text);
    this.command = command;
    this.description = ChatManager.colorRawMessage(description);
  }
}
