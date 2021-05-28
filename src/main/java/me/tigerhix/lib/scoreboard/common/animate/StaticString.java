package me.tigerhix.lib.scoreboard.common.animate;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StaticString implements AnimatableString {

    String string;

    @Override
    public String current() {
        return string;
    }

    @Override
    public String previous() {
        return string;
    }

    @Override
    public String next() {
        return string;
    }

}
