package org.spoutcraft.client.nterface.render.stage;

import java.util.ArrayList;
import java.util.List;

import org.spout.renderer.api.Camera;
import org.spout.renderer.api.Creatable;
import org.spout.renderer.api.Pipeline;
import org.spout.renderer.api.Pipeline.PipelineBuilder;
import org.spout.renderer.api.data.Uniform.Matrix4Uniform;
import org.spout.renderer.api.gl.FrameBuffer;
import org.spout.renderer.api.gl.FrameBuffer.AttachmentPoint;
import org.spout.renderer.api.gl.GLFactory;
import org.spout.renderer.api.gl.Texture;
import org.spout.renderer.api.model.Model;

import org.spoutcraft.client.nterface.render.Renderer;

/**
 *
 */
public class RenderModelsStage extends Creatable {
    private final Renderer renderer;
    private final FrameBuffer frameBuffer;
    private Texture colorsOutput;
    private Texture normalsOutput;
    private Texture depthsOutput;
    private Texture vertexNormalsOutput;
    private Texture materialsOutput;
    private final List<Model> models = new ArrayList<>();
    private final Camera camera = Camera.createPerspective(Renderer.FIELD_OF_VIEW, Renderer.WINDOW_SIZE.getFloorX(), Renderer.WINDOW_SIZE.getFloorY(), Renderer.NEAR_PLANE, Renderer.FAR_PLANE);
    private Pipeline pipeline;

    public RenderModelsStage(Renderer renderer) {
        this.renderer = renderer;
        final GLFactory glFactory = renderer.getGLFactory();
        frameBuffer = glFactory.createFrameBuffer();
    }

    @Override
    public void create() {
        if (isCreated()) {
            throw new IllegalStateException("Render models stage has already been created");
        }
        // Create the frame buffer
        frameBuffer.attach(AttachmentPoint.COLOR0, colorsOutput);
        frameBuffer.attach(AttachmentPoint.COLOR1, normalsOutput);
        frameBuffer.attach(AttachmentPoint.COLOR2, vertexNormalsOutput);
        frameBuffer.attach(AttachmentPoint.COLOR3, materialsOutput);
        frameBuffer.attach(AttachmentPoint.DEPTH, depthsOutput);
        frameBuffer.create();
        // Create the pipeline
        pipeline = new PipelineBuilder().useCamera(camera).bindFrameBuffer(frameBuffer).clearBuffer().renderModels(models).unbindFrameBuffer(frameBuffer).build();
        // Update the state to created
        super.create();
    }

    @Override
    public void destroy() {
        checkCreated();
        frameBuffer.destroy();
        colorsOutput.destroy();
        normalsOutput.destroy();
        depthsOutput.destroy();
        vertexNormalsOutput.destroy();
        materialsOutput.destroy();
        super.destroy();
    }

    public void render() {
        checkCreated();
        pipeline.run(renderer.getContext());
    }

    public Texture getColorsOutput() {
        return colorsOutput;
    }

    public void setColorsOutput(Texture texture) {
        texture.checkCreated();
        colorsOutput = texture;
    }

    public Texture getNormalsOutput() {
        return normalsOutput;
    }

    public void setNormalsOutput(Texture texture) {
        texture.checkCreated();
        normalsOutput = texture;
    }

    public Texture getDepthsOutput() {
        return depthsOutput;
    }

    public void setDepthsOutput(Texture texture) {
        texture.checkCreated();
        depthsOutput = texture;
    }

    public Texture getVertexNormalsOutput() {
        return vertexNormalsOutput;
    }

    public void setVertexNormalsOutput(Texture texture) {
        texture.checkCreated();
        vertexNormalsOutput = texture;
    }

    public Texture getMaterialsOutput() {
        return materialsOutput;
    }

    public void setMaterialsOutput(Texture texture) {
        texture.checkCreated();
        materialsOutput = texture;
    }

    public Camera getCamera() {
        return camera;
    }

    /**
     * Adds a model to the renderer.
     *
     * @param model The model to add
     */
    public void addModel(Model model) {
        model.getUniforms().add(new Matrix4Uniform("previousModelMatrix", model.getMatrix()));
        models.add(model);
    }

    /**
     * Removes a model from the renderer.
     *
     * @param model The model to remove
     */
    public void removeModel(Model model) {
        models.remove(model);
    }

    /**
     * Removes all the models from the renderer.
     */
    public void clearModels() {
        models.clear();
    }

    public List<Model> getModels() {
        return models;
    }
}
