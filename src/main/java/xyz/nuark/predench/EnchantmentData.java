package xyz.nuark.predench;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.datafixers.types.templates.CompoundList;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.nbt.*;
import net.minecraft.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.DataOutput;
import java.io.IOException;
import java.util.LinkedList;

public class EnchantmentData {
    private final LinkedList<LinkedList<Enchantment>> tiers;

    public EnchantmentData() {
        this.tiers = new LinkedList<>();
        tiers.add(new LinkedList<>());
        tiers.add(new LinkedList<>());
        tiers.add(new LinkedList<>());
    }

    public void addToTier(int tier, Enchantment enchantment) {
        tiers.get(tier).add(enchantment);
    }

    public LinkedList<Enchantment> getTier(int tier) {
        return tiers.get(tier);
    }

    public ListTag convertToBookPages() {
        String[] pages = {"", "", ""};

        for (int i = 0; i < 3; i++) {
            if (tiers.get(i).isEmpty()) {
                pages[i] += "Tier " + (i + 1) + "\n";
                pages[i] += "======\n";
                pages[i] += "No enchantments";
            } else {
                pages[i] += "Tier " + (i + 1) + " at " + tiers.get(i).get(0).cost + "\n";
                pages[i] += "======\n";
                for (Enchantment enchantment : tiers.get(i)) {
                    pages[i] += enchantment.name + "\n";
                }
            }
        }

        ListTag list = new ListTag();

        for (int i = 0; i < 3; i++) {
            JsonObject json = new JsonObject();
            json.addProperty("text", pages[i]);
            list.add(StringTag.valueOf(json.toString()));
        }

        return list;
    }

    public record Enchantment(String name, int level, int cost) {
    }
}
