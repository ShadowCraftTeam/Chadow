#!/usr/bin/python3
# -*- coding: utf-8 -*-
import json
import os
import sys
import requests
import database
import zipfile

def extract(path, to, delete=True):
    print("Decompressing zip file: %s" % path)
    zip = zipfile.ZipFile(path)
    zip.extractall(to)
    print("Decompressed file successfully.")
    zip.close()
    if delete:
        os.remove(path)

def is_argument_value(v):
    return v.startswith('-')

def isnumber(obj):
    try:
        int(obj)
        return True
    except:
        return False


def has_args_filter(value, f):
    try:
        if isinstance(f, dict) is True:
            do = f[value]
        else:
            f.index(value)
        return True
    except:
        return False


def argv_dict(it, current):
    try:
        if is_argument_value(it[current + 1]) is True:
            return None
        else:
            return it[current + 1]
    except IndexError:
        return None

def define_argument_value(argv, name, default=None, flag=None):
    target = None
    try:
        if has_args_filter(name, argv) is True:
            target = argv.get(name)

        if target is None:
            if flag is not None:
                target = argv.get(flag)
        else:
            return target

        if target is None:
            return default
        else:
            return target

    except Exception as e:
        return default

def disting_args(args, *it, path_ignored=True):
    kv = {}
    unknown = []
    for i, v in enumerate(args):
        if i == 0:
            if is_argument_value(v) is False:
                unknown.append(v)
            else:
                if has_args_filter(v, it) is True:
                    kv[v] = True
                else:
                    kv[v] = argv_dict(args, i)
        else:
            if is_argument_value(v) is True:
                if has_args_filter(v, it) is True:
                    kv[v] = True
                else:
                    kv[v] = argv_dict(args, i)
            else:
                if kv.get(args[i - 1]) is v:
                    continue
                else:
                    unknown.append(v)

    if path_ignored is False:
        unknown = sys.argv[:1] + unknown
    return kv, unknown


def downloadfrom(url, output=None, filetype=None, ignore_exist=False, output_log=True):
    if len(url) == 0 or url is None:
        print("Can't download because the parameter 'url' is emptry or None")
        return None

    if output is None:
        """ """
        output = url.split('/')[-1]

    if filetype is None:
        """  """
        filetype = url.split('.')[-1]

    fvaild = __check__
    try:
        fvaild = getattr(sys.modules[__name__], 'vaild{ftype}'.format(ftype=filetype))
    except:
        if output_log:
            print("WARNING: filetype check function is not defined '%s'" % filetype)

    if os.path.exists(output) is True:
        if fvaild(output) is False:
            if output_log:
                print("{file}'s invaild {ftype} format, Remove it".format(file=output, ftype=filetype))
            os.remove(output)
        else:
            if output_log:
                print('{file} was already existed, Skip downloading the file'.format(file=output))
            return output

    resp = requests.get(url)

    f = None
    with open(output, "wb") as file:
        file.write(resp.content)
        f = file
        f.close()
    if fvaild(output) is True:
        if output_log:
            print('download done.')
        return f.name
    else:
        if output_log:
            print('The download process is complete but maybe invaild file or incomplete. Please try again.')
        return None


def __check__(obj):
    return obj is not None


def remove_raw_file(o):
    delete = input("Do you want to remove raw file? yes/no (default: yes):")
    if delete == "":
        delete = "yes"

    if delete == "yes":
        os.remove(o.raw_file)
        print("{filename} removed sucessfully.".format(filename=o.raw_file))
        o.raw_file = None
    else:
        print("canceled.")

def vaildjson(filepath):
    try:
        fs = open(filepath,encoding='utf8').read()
        json.loads(fs)
        return True
    except:
        return False


def vaildzip(filepath):
    return zipfile.is_zipfile(filepath)


def vaildcsv(filepath):
    import csv
    try:
        f = open(filepath, 'r', encoding='utf-8')
        reader = csv.reader(f)
        f.close()
        return True
    except:
        return False


def vaildxml(filepath):
    import xml
    try:
        xml.etree.ElementTree.parse(filepath)
        return True
    except:
        return False