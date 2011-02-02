package com.code.aon.asset.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.asset.Asset;
import com.code.aon.asset.AssetActivity;
import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;

/**
 * @author Consulting & Development. eagirrezabal - 04/02/2009
 *
 */
public class AssetAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-TRUNK/aon-asset/src/main/java/com/code/aon/asset/dao/IAssetAlias.java");
		String[] classes = new String[] {
				Asset.class.getName(),
				AssetActivity.class.getName(),
			};
		AliasWriter writer = new AliasWriter("com.code.aon.asset.dao");
		HibernateUtil.getSessionFactory(null);
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}