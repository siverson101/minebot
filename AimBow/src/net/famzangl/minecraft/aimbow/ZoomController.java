package net.famzangl.minecraft.aimbow;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

public class ZoomController {

	protected float yaw;
	protected float pitch;
	protected float zoom = 1;

	public void reset() {
		yaw = pitch = 0;
		zoom = 1;
	}

	public void zoomTowards(Vec3 target, float partialTicks) {
		EntityLivingBase entitylivingbase = Minecraft.getMinecraft().renderViewEntity;
		Vec3 player = entitylivingbase.getPosition(partialTicks);
		// Vec3 looking = entitylivingbase.getLook(partialTicks);
		Vec3 toTarget = target.addVector(-player.xCoord, -player.yCoord,
				-player.zCoord);
		Vec3 marking = toTarget.normalize();
//		System.out.println("Current d: " + marking);
		marking.rotateAroundY((float) (entitylivingbase.rotationYaw / 180 * Math.PI));
//		System.out.println("Current d: " + marking);
		marking.rotateAroundX((float) (entitylivingbase.rotationPitch / 180 * Math.PI));
//		System.out.println("Current d: " + marking);

		Vec3 xz = Vec3.createVectorHelper(marking.xCoord, 0, marking.zCoord)
				.normalize();
		Vec3 xy = Vec3.createVectorHelper(0, marking.yCoord, marking.zCoord)
				.normalize();
		double angX = Math.asin(xz.xCoord);
		double angY = Math.asin(xy.yCoord);

		double dist = toTarget.lengthVector();
		zoom = (float) (zoom * .9 + (dist / 20) * .1);
		pitch = (float) (angY * 5);
		if (zoom < 1) {
			zoom = 1;
			yaw = 0;
			pitch = 0;
		}
	}

	public void apply(float factor) {
		setERField("cameraYaw", factor * yaw);
		setERField("cameraPitch", factor * pitch);
		setERField("cameraZoom", factor * zoom + (1 - factor));
	}

	private void setERField(String name, float value) {
		try {
			Field field = EntityRenderer.class.getDeclaredField(name);
			field.setAccessible(true);
			field.setFloat(Minecraft.getMinecraft().entityRenderer, value);
		} catch (Throwable e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
