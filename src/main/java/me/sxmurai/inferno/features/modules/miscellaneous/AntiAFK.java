package me.sxmurai.inferno.features.modules.miscellaneous;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;

import java.util.ArrayList;
import java.util.Random;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.features.settings.Setting;
import net.minecraft.util.EnumHand;

@Module.Define(name = "AntiAFK", description = "Stops you from getting kicked by the AFK timer on servers that have it")
public class AntiAFK extends Module {

public int minChat = 0;
public int maxChat = 99999999;
public int chatRandom = (int)Math.floor(Math.random()*(maxChat-minChat+1)+minChat);
private static Thread thread;
	
	public final Setting<Double> delay = this.register(new Setting<>("Delay", 2));
	public final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
	public final Setting<Boolean> punch = this.register(new Setting<>("Punch", true));
	public final Setting<Boolean> jump = this.register(new Setting<>("Jump", true));
	public final Setting<Boolean> random = this.register(new Setting<>("Rotate", true));
	public final Setting<Boolean> chat = this.register(new Setting<>("ChatMessage", true));
	
	@Override
	public void onEnabled() {
		thread = new Thread() {
			public void run() {
				while(thread != null && thread.equals(this)) {
					loop();
					
					modules.sleep((int)(delay.getValue() * 1000));
				}
			}
		};
		
		thread.start();
	}
	
	@Override
	public void onDisabled() {
		thread = null;
	}
	
	public static int random(int min, int max) {
    	return new Random().nextInt(min + max) - min;
    }
	
	public void loop() {
		if (mc.player == null) {
			return;
		}
		
		ArrayList<Integer> actions = new ArrayList<Integer>();
		if (rotate.getValue()) actions.add(1);
		if (punch.getValue()) actions.add(2);
		if (jump.getValue()) actions.add(3);
		if (chat.getValue()) actions.add(4);
		
		if (!actions.isEmpty()) {
			if (random.getValue()) {
				int action = actions.get(random(0, actions.size()));
				doAction(action);
			} else {
				for (int action : actions) {
					doAction(action);
				}
			}
		}
	}

	public static void doAction(int id) {
		if (id == 1) {
			mc.player.rotationYaw = random(0, 170);
			mc.player.rotationPitch = random(0, 80);
		} else if (id == 2) {
			mc.player.swingArm(EnumHand.MAIN_HAND);
		} else if (id == 3) {
			mc.player.jump();
		} else if (id == 4) {
			mc.player.sendChatMessage("im sending this message so i dont get afk kicked thanks to inferno client, so yeah, whatever i guess " + chatRandom);
		}
	}

}
