package com.pepej.murdermystery.utils.engine;

import com.pepej.murdermystery.MurderMystery;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptEngine {

    private final javax.script.ScriptEngine scriptEngine;

    public ScriptEngine() {
        scriptEngine = new ScriptEngineManager().getEngineByName("js");
    }

    public void setValue(String value, Object valueObject) {
        scriptEngine.put(value, valueObject);
    }

    public void execute(String executable) {
        try {
            scriptEngine.eval(executable);
        } catch(ScriptException e) {
            MurderMystery.getInstance().getPluginLogger().severe("Script failed to parse expression! Expression was written wrongly!");
            MurderMystery.getInstance().getPluginLogger().severe("Expression value: " + executable);
            MurderMystery.getInstance().getPluginLogger().severe("Error log:");
            e.printStackTrace();
            MurderMystery.getInstance().getPluginLogger().severe("---- THIS IS AN ISSUE BY USER CONFIGURATION NOT AUTHOR BUG ----");
        }
    }

}