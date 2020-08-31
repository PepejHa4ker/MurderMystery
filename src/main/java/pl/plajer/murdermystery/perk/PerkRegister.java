package pl.plajer.murdermystery.perk;

import com.google.common.reflect.ClassPath;
import lombok.Getter;
import lombok.SneakyThrows;
import pl.plajer.murdermystery.MurderMystery;

import java.util.Set;
import java.util.TreeSet;

public class PerkRegister {

    @Getter
    private static boolean inited = false;

    @Getter
    private static final Set<Perk> cachedPerks = new TreeSet<>();

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
                Class<PerkAnn> a = PerkAnn.class;
                if (c.isAnnotationPresent(a)) {
                    if (c.getAnnotation(a).shouldBeLoaded()) {
                        PerkRegister.getCachedPerks().add((Perk) c.newInstance());
                    } else {
                        MurderMystery.getInstance().getPluginLogger().warn("Found unuse Perk: " + c.getSimpleName());
                    }
                    inited = true;
                }
            }
        }
    }


}
