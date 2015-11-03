package us.drullk.shizzel.forestry.bees.alleles;

import forestry.api.genetics.IEffectData;
import net.minecraft.nbt.NBTTagCompound;

public class EffectData implements IEffectData {

	private int[] intData;
	private boolean[] boolData;
	private float[] floatData;

	public EffectData(int intCount, int boolCount, int floatCount) {
		this.intData = new int[intCount];
		this.boolData = new boolean[boolCount];
		this.floatData = new float[floatCount];
	}

	@Override public void readFromNBT(NBTTagCompound nbttagcompound) {
	}

	@Override public void writeToNBT(NBTTagCompound nbttagcompound) {
	}

	@Override public void setInteger(int index, int val) {
		if (index >= 0 && index < this.intData.length) {
			this.intData[index] = val;
		}
	}

	@Override public void setFloat(int index, float val) {
		if (index >= 0 && index < this.floatData.length) {
			this.floatData[index] = val;
		}
	}

	@Override public void setBoolean(int index, boolean val) {
		if (index >= 0 && index < this.boolData.length) {
			this.boolData[index] = val;
		}
	}

	@Override public int getInteger(int index) {
		int val = 0;
		if (index >= 0 && index < this.intData.length) {
			val = this.intData[index];
		}
		return val;
	}

	@Override public float getFloat(int index) {
		float val = 0f;
		if (index >= 0 && index < this.floatData.length) {
			val = this.floatData[index];
		}
		return val;
	}

	@Override public boolean getBoolean(int index) {
		boolean val = false;
		if (index >= 0 && index < this.boolData.length) {
			val = this.boolData[index];
		}
		return val;
	}
}