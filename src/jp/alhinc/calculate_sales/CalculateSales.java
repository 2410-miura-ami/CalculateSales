package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//ArrayListインポート
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";
	private static final String FILE_NOT_SERIAL_NUMBER = "売上ファイル名が連番になっていません";


	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			return;
		}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)


		//listFilesを使用してfilesという配列に、指定したパスに存在する
		//全てのファイル(または、ディレクトリ)の情報を格納します。

		File[] files = new File(args[0]).listFiles();

		//先にファイルの情報を格納する List(ArrayList) を宣言

		List<File> rcdFiles = new ArrayList<>();

		//指定したパスに存在する全てのファイル(またはディレクトリ)の数だけ繰り返されるfor文

		for(int i = 0; i < files.length; i++) {

			//matches を使用してファイル名が「数字8桁.rcd」なのか判定します。
			if(files[i].getName().matches("^[0-9]{8}.rcd$")){

				//trueの場合(売上ファイルの条件に当てはまったものだけ、List(ArrayList) に追加します。
				rcdFiles.add(files[i]);
			}
		}
		//エラー処理2-1
		//OS問わず、正常に動作させるためには連番チェックを行う前に、売上ファイルを保持しているListをソートする
		Collections.sort(rcdFiles);

		//繰り返し回数は売上ファイルのリストの数よりも1つ小さい数（比較回数はファイルの数より１つ少なくなるから）
		for(int i = 0; i < rcdFiles.size() - 1; i++) {
			//比較する2つのファイル名の先頭から数字の8文字を切り出し、int型に変換
			int former = Integer.parseInt(rcdFiles.get(i).substring(0, 8));
			int later = Integer.parseInt(rcdFiles.get(i + 1).substring(0,8));

			if((later - former) != 1) {
				System.out.println(FILE_NOT_SERIAL_NUMBER);
			}

		}


		//2-2
		// 売上ファイルがrcdFilesに複数存在しているので、その分繰り返す
		for(int i = 0; i < rcdFiles.size(); i++) {

			//支店定義ファイル読み込み(readFileメソッド)を参考に売上ファイルを読み込む
			//売上ファイルの内容は支店定義ファイルと異なるため、売上ファイルを読み込めるように処理内容変える

			BufferedReader br = null;

			try {

				FileReader fr = new FileReader(rcdFiles.get(i));
				br = new BufferedReader(fr);

				String line;
				List<String> contents = new ArrayList<String>();
				//一行ずつ読み込む
				while((line = br.readLine()) != null) {
					//保持
					contents.add(line);

				}

				//ファイルから読み込んだ情報は、内容にかかわらず一律で文字列(String) として扱われます
				//売上ファイルの売上金額は、Longとして扱うため、Mapに追加するためには型を変換する必要があり
				long fileSale = Long.parseLong(contents.get(1));

				//売上ファイルから読み込んだ売上⾦額を加算して、
				//Mapに追加するには既にMapにある売上⾦額を取得する必要があり

				Long saleAmount = branchSales.get(contents.get(0)) + fileSale;

				//加算した売上金額をMapにput
				branchSales.put(contents.get(0), saleAmount);


			} catch(IOException e) {
				System.out.println(UNKNOWN_ERROR);
				return;

			} finally {
				// ファイルを開いている場合
				if(br != null) {
					try {
						// ファイルを閉じる
						br.close();
					} catch(IOException e) {
						System.out.println(UNKNOWN_ERROR);
						return;

					}
				}
			}

		}



		// 支店別集計ファイル書き込み処理
		if(!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}

	}

	/**
	 * 支店定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		BufferedReader br = null;

		try {

			File file = new File(path, fileName);
			//エラー処理。ファイルの存在チェック(ここに処理を入れることでファイルがない場合、読み込みをする前に終了できる。）
			if(!file.exists()) {
				System.out.println(FILE_NOT_EXIST);
				return false;
			}

			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。(処理内容1-2)

				//splitメソッドで一行ずつ読み込んだ値を区切る（今回は","で区切る）
				String[] items = line.split(",");

				//エラー処理。ファイルのフォーマットをチェック
				if((items.length != 2) || (items[0].matches("^[0-9]{3}$"))) {
					System.out.println(FILE_INVALID_FORMAT);
					return false;
				}

				branchNames.put(items[0], items[1]);
				branchSales.put(items[0], 0L);

			}

		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(br != null) {
				try {
					// ファイルを閉じる
					br.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		// ※ここに書き込み処理を作成してください。(処理内容3-1)

		BufferedWriter bw = null;

		try {
			//ファイルを作成し、書き込む処理
			File file = new File(path, fileName);

			FileWriter fw = new FileWriter(file);

			bw = new BufferedWriter(fw);

			for(String key: branchNames.keySet()) {

				bw.write(key + "," + branchNames.get(key) + "," + branchSales.get(key));
				bw.newLine();
			}

		} catch(IOException e){
			//エラーメッセージの表示
			System.out.println(UNKNOWN_ERROR);
			return false;

		} finally {
			//ファイルを開いた場合は、ファイルを閉じる処理
			if(bw != null) {
				try {
					//ファイルを閉じる
					bw.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}

		}
		return true;
	}

}
