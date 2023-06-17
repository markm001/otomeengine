package com.ccat.core.challenge;

import com.ccat.core.WindowManager;
import com.ccat.core.renderer.TextureLoader;
import com.ccat.core.util.FileReaderUtil;
import org.joml.Vector3f;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class TexturedCubes {
    private final HashMap<TexturesChallenge, Vector3f> cubes = new HashMap<>();
    private final List<TextureLoader> textures = new ArrayList<>();

    private final List<String> paths = List.of(
            "textures/test_texture_03.png",
            "textures/test_texture_02.jpg",
            "textures/test_texture_04.jpg"
    );


    public TexturedCubes(WindowManager window, int amount) {
            paths.forEach( f -> {
                try {
                    ByteBuffer bufferedImg = FileReaderUtil.readImage(f);
                    textures.add(new TextureLoader(bufferedImg, GL_RGB, GL_NEAREST));
                } catch (IOException e) {
                    throw new RuntimeException("Unable to load Texture image from path.");
                }
            });

        float offset = (float)amount / 2;
        Random r = new Random();

        for (int z = 0; z < amount; z++) {
            for (int x = 0; x < amount; x++) {

                Vector3f position = new Vector3f((float)x-offset, 0f, (float)z-offset);
                //10cubes
                int randomNum = r.nextInt(0, textures.size());
                TexturesChallenge cube = new TexturesChallenge(window, textures.get(randomNum));
                cubes.put(cube, position);
                cube.initNewChallenge();
            }
        }
    }

    public void drawCubes() {
        cubes.forEach(TexturesChallenge::drawCube);
    }

    public void disposeCubes() {
        cubes.forEach((c,v) -> c.disposeCurrentChallenge());
    }
}
