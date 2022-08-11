package invoker54.arsgears.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class CircleRender {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Draw an arc centred around the zero point.  Setup translatef, colour and line width etc before calling.
     * @param radius
     * @param startAngle clockwise starting from 12 O'clock (degrees)
     * @param endAngle (degreesO
     */
    public static void drawArc(MatrixStack stack, int origX, int origY, double radius, double startAngle, double endAngle, int colorCode) {
        stack.pushPose();
        Matrix4f lastPos = stack.last().pose();
        //This is how much the angle will increase
        final double angleIncrement = Math.toRadians(5.0);
        //This will flip the direction of the circle
        float direction = (endAngle >= startAngle) ? 1.0F : -1.0F;
        //Delta angle is just the difference between start and end
        double deltaAngle = Math.abs(endAngle - startAngle);
        //This makes it so the difference stays between 360
        deltaAngle %= 360.0;
        //If the difference is 360, this will make it so
        deltaAngle = (deltaAngle == 0 ? 360 : deltaAngle);
        startAngle = direction < 0 ? endAngle : startAngle;

        //All this did was move the start angle 1 number up or down
        startAngle -= Math.floor(startAngle / 360.0);

        //This converts the numbers into actual angle data
        startAngle = Math.toRadians(startAngle);
        deltaAngle = Math.toRadians(deltaAngle);

        double x, y;
        //How many degrees has been renderer already
        double arcPos = 0;
        boolean arcFinished = false;

        //The coloring of the angle
        float f3 = (float) (colorCode >> 24 & 255) / 255.0F;
        float f = (float) (colorCode >> 16 & 255) / 255.0F;
        float f1 = (float) (colorCode >> 8 & 255) / 255.0F;
        float f2 = (float) (colorCode & 255) / 255.0F;

        //Setting up the render system
        RenderSystem.disableTexture();
        RenderSystem.disableCull();
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
        bufferbuilder.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);

        //Places a point in the middle of the circle only if it isn't going to be a full circle
        if(deltaAngle < 360)
            bufferbuilder.vertex(lastPos, origX, origY, 0).color(f, f1, f2, f3).endVertex();

        do {
            //Trunc angle is pretty much the current degree we are on. (can't be higher than delta angle)
            double truncAngle = Math.min(arcPos, deltaAngle);
            x = origX + (radius * Math.sin(startAngle + (direction * truncAngle)));
            y = origY + (-radius * Math.cos(startAngle + truncAngle));
            //System.out.println("X Coordinates are: " + String.valueOf(x) + "," + String.valueOf(y));

            bufferbuilder.vertex(lastPos, (float) x, (float) y, 0).color(f, f1, f2, f3).endVertex();
            //GL11.glVertex3d(x, y, zLevel);

            //if the current angle (arcpos) is greater than or equal to delta angle
            arcFinished = (arcPos >= deltaAngle);
            //Increases the current angle by angleIncrement for the next cycle
            arcPos += angleIncrement;
        } while (!arcFinished && arcPos <= Math.toRadians(360.0)); // arcPos test is a fail safe to prevent infinite loop in case of problem with angle arguments

        bufferbuilder.end();
        WorldVertexBufferUploader.end(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
        stack.popPose();

    }

    public static void drawArcLine(MatrixStack stack, int origX, int origY, double radius, double angle, int colorCode) {
        stack.pushPose();
        Matrix4f lastPos = stack.last().pose();

        double x, y;
        angle = Math.toRadians(angle);

        //The coloring of the angle
        float f3 = (float) (colorCode >> 24 & 255) / 255.0F;
        float f = (float) (colorCode >> 16 & 255) / 255.0F;
        float f1 = (float) (colorCode >> 8 & 255) / 255.0F;
        float f2 = (float) (colorCode & 255) / 255.0F;

        //Setting up the render system
        RenderSystem.disableTexture();
        RenderSystem.disableCull();
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuilder();
        bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        RenderSystem.lineWidth(3);

        //Places a point in the middle of the circle only if it isn't going to be a full circle
        bufferbuilder.vertex(lastPos, origX, origY, 0).color(f, f1, f2, f3).endVertex();

        x = origX + (radius * Math.sin(angle));
        y = origY + (-radius * Math.cos(angle));
        bufferbuilder.vertex(lastPos, (float) x, (float) y, 0).color(f, f1, f2, f3).endVertex();

        bufferbuilder.end();
        WorldVertexBufferUploader.end(bufferbuilder);
        RenderSystem.lineWidth(1);
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
        stack.popPose();

    }
}
