package pl.plajer.murdermystery.perk;

import com.google.common.reflect.ClassPath;
import lombok.Getter;
import lombok.SneakyThrows;

public class PerkRegister {

    @Getter
    private static boolean inited = false;

    @SneakyThrows
    public void initPerks() {
        ClassLoader loader = getClass().getClassLoader();
        if (loader == null) {
            return;
        }
        if (inited) {
            throw new IllegalStateException("Perks already has been loaded.");
        }
        ClassPath classpath = ClassPath.from(loader);
        for (ClassPath.ClassInfo foo : classpath.getTopLevelClassesRecursive("pl.plajer.murdermystery.perk.perks")) {
            if (!foo.getName().equals(getClass().getName())) {
                Class<?> c = foo.load();
                if (c.isAnnotationPresent(PerkAnn.class)) {
                    Perk.getAllPerks().add((Perk) c.newInstance());
                    inited = true;
                }
            }
        }
    }


}
