package com.ccat.core.renderer;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL45.glBindTextureUnit;
import static org.lwjgl.stb.STBImage.*;

public class TextureLoader {
    private final int textureId;

    public TextureLoader(ByteBuffer imgBuffer, int internalFormat, int filter) {
        //Generate Texture
        this.textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        //Set Texture Params:
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); //u
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT); //v
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter); // stretching
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter); // shrinking

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true);
            ByteBuffer data = stbi_load_from_memory(imgBuffer, width,height,channels,0);
            if (data != null) {
                //Upload Image
                glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
                glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width.get(), height.get(), 0, internalFormat, GL_UNSIGNED_BYTE, data);
                glGenerateMipmap(GL_TEXTURE_2D);
            } else {
                String error = stbi_failure_reason();
                throw new RuntimeException("Failed to load Texture - Reason:" + error);
            }

            stbi_image_free(data);
        }
    }

    /** Binds the Texture */
    public void bind(int textureSlot) {
        glBindTextureUnit(textureSlot, textureId);
    }

    /** Unbinds the Texture */
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void destroy() {
        glDeleteTextures(textureId);
    }
}
