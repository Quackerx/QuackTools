package com.quack.tools;

import com.quack.tools.modules.ShieldDestroyer;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class QuackTools extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("QuackTools");

    @Override
    public void onInitialize() {
        LOG.info("Initializing QuackTools..");


        Modules.get().add(new ShieldDestroyer());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.quack.tools";
    }

}
