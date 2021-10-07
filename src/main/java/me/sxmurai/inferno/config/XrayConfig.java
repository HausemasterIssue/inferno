package me.sxmurai.inferno.config;

import me.sxmurai.inferno.managers.FileManager;
import me.sxmurai.inferno.managers.XrayManager;
import net.minecraft.block.Block;
import org.json.JSONArray;

public class XrayConfig extends BaseConfig {
    private final XrayManager manager;

    public XrayConfig(XrayManager manager) {
        super(FileManager.getInstance().getClientFolder().resolve("xray_blocks.json"));
        this.manager = manager;
    }

    @Override
    public void save() {
        JSONArray json = new JSONArray();

        if (this.manager.getBlocks().isEmpty()) {
            XrayManager.defaultBlocks().forEach(this.manager::add);
        }

        for (Block block : this.manager.getBlocks()) {
            json.put(block.getRegistryName());
        }

        this.files.writeFile(this.path, json.toString(4));
    }

    @Override
    public void load() {
        String config = this.read();
        if (config == null || config.isEmpty()) {
            this.save();
            return;
        }

        for (Object obj : new JSONArray(config)) {
            if (!(obj instanceof String)) {
                continue;
            }

            this.manager.add(Block.getBlockFromName((String) obj));
        }
    }
}
