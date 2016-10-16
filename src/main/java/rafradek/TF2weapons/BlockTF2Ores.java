package rafradek.TF2weapons;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.BlockTF2Ores.EnumOreType;

public class BlockTF2Ores extends BlockOre {

	public enum EnumOreType implements IStringSerializable{

		COPPER("copper"),
		LEAD("lead"),
		AUSTRALIUM("australium");
		
		private String name;
		
		EnumOreType(String name){
			this.name=name;
		}
		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return name;
		}

	}

	public static final PropertyEnum<EnumOreType> TYPE = PropertyEnum.<EnumOreType>create("oreType", EnumOreType.class);
	
	public BlockTF2Ores() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        for (EnumOreType enumoretype : EnumOreType.values())
        {
            list.add(new ItemStack(itemIn, 1, enumoretype.ordinal()));
        }
    }
	public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(TYPE, EnumOreType.values()[meta]);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumOreType)state.getValue(TYPE)).ordinal();
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {TYPE});
    }
}
