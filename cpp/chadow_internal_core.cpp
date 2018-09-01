#include <iostream>
#include <algorithm>
#include <fstream>
#include <string>

#include <boost/filesystem.hpp>
#include <boost/algorithm/string/replace.hpp>

#include "json.hpp"
#include "picosha2.h"
#include "io_github_shadowcreative_EntityUnitCollection.h"

using namespace std;

JNIEXPORT jstring JNICALL Java_io_github_shadowcreative_eunit_EntityUnitCollection_onChangeHandler0(JNIEnv *env, jobject object, jstring jstr)
{
	namespace fs = boost::filesystem;
	using json = nlohmann::json;

	json j = "{}"_json;
	
	string str_path(env->GetStringUTFChars(jstr, nullptr));
	boost::replace_all(str_path, ".", "");

	vector<unsigned char> hash(picosha2::k_digest_size);

	fs::path current_path = fs::path(fs::current_path());
	for (auto &p : fs::directory_iterator(current_path))
	{
		ifstream file(fs::path(p).string(), std::ios::binary);
		picosha2::hash256(file, hash.begin(), hash.end());
		j[fs::path(p).string()] = { {"hash", picosha2::bytes_to_hex_string(hash.begin(), hash.end())}, {"isChanged:", false} };
		file.close();
	}

	std::string result = j.dump();
	return env->NewStringUTF(result.c_str());
}