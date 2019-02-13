package opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryStack.stackPush;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Vbo
{
	private final int vboId;
	private final int type;

	private Vbo(int vboId, int type){
		this.vboId = vboId;
		this.type = type;
	}

	public static Vbo create(int type){
		int id = GL15.glGenBuffers();
		return new Vbo(id, type);
	}

	public void bind(){
		GL15.glBindBuffer(type, vboId);
	}

	public void unbind(){
		GL15.glBindBuffer(type, 0);
	}

	public void storeData(float[] data){
		try ( MemoryStack stack = stackPush() )
		{
			FloatBuffer buffer = stack.mallocFloat(data.length);
			buffer.put(data).flip();
			storeData(buffer);
		}
	}

	public void storeData(int[] data){
		try ( MemoryStack stack = stackPush() )
		{
			IntBuffer buffer = stack.mallocInt(data.length);
			buffer.put(data).flip();
			storeData(buffer);
		}
	}

	public void storeData(IntBuffer data){
		GL15.glBufferData(type, data, GL15.GL_STATIC_DRAW);
	}

	public void storeData(FloatBuffer data){
		GL15.glBufferData(type, data, GL15.GL_STATIC_DRAW);
	}

	public void delete(){
		GL15.glDeleteBuffers(vboId);
	}
}
