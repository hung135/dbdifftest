import os
import sys
import re
import math
import argparse
import requests
from github import Github

def _error_out(msg):
    print(msg)
    sys.exit(1)

def _file_exist(path):
    return os.access(path, os.W_OK)

def _get_release_based_tag(releases, tag):
    release = [release for release in releases if release.tag_name == tag][0]
    if not release:
        _error_out("Release tag %s does not exists" % args.tag)
    return release

def delete_all(releases):
    last_release = releases[0]
    print("Not deleting {0}".format(last_release.tag_name))
    count = 0
    for release in releases:
        if last_release.tag_name != release.tag_name:
            count+=1
            release.delete_release()
            print("\t\tDeleted {0}".format(release.tag_name))
    print("Deleted {0}".format(count))
    

def create_release(repository, releases, name, message, tag=None):
     
    if not tag:
        try:
  
 
            #tag = re.findall(".\d", releases[0].tag_name)
            print(releases[0].tag_name)
            for rel in releases:
                if re.match("v\d+\.\d+\.\d+",rel.tag_name):
                    major,minor,micro = rel.tag_name.split(".")
                    break
            
            if int(micro)==99:
                micro = 0
                minor = int(minor)+1
            else:
                micro = int(micro)+1
            # if ".9" in tag:
            #     indx = tag.index(".9")
            #     tag[indx] = ".11"
            #     tag = "".join(tag)
            # else:
            #     tag[-1] = "." + str(int(tag[-1].split(".")[-1]) + 1)
            tag = "{0}.{1}.{2}".format(major,minor,micro)
        except IndexError:
            tag = "v0.0.1"
     
     
    release = repository.create_git_release(tag, name, message, prerelease=True)
    return (release, tag)

def upload_asset(filep, tag=None, release=None, releases=None, version=None):
    if not _file_exist(filep):
        print(filep)
        _error_out("File not found")
    if not release:
        release = _get_release_based_tag(releases, tag)
    if version:
        path, file_type = os.path.splitext(filep)
        file_name = os.path.basename(path)
        file_name = "{0}_{1}{2}".format(file_name, version, file_type)
        new_path = os.path.abspath(os.path.join(os.path.dirname(path), file_name))
        os.rename(filep, new_path)
        filep = new_path

    filep = os.path.abspath(filep)
    release.upload_asset(filep)

def download_asset(filep, tag, releases):
    try:
        if not _file_exist(filep):
            filep = os.path.abspath(filep)
            os.mkdir(filep)
        release = _get_release_based_tag(releases, tag)
        uri = release.get_assets()[0].browser_download_url
        response = requests.get(uri, allow_redirects=True)
        fname = re.findall("filename=(.+)", response.headers.get("content-disposition"))[0]
        print("Writing file %s" % fname)
        filep = os.path.abspath(filep + "/%s" % fname)
        open(filep, "wb").write(response.content)
    except Exception as e:
        _error_out("Couldn't download file to %s based on tag %s, \n e: %s" % (filep, tag, e))

def parse_cli():
   parser = argparse.ArgumentParser(description='Manages release assets for a Github release')

   required_groups = parser.add_argument_group("Required (key not required if downloading")
   required_groups.add_argument("-k", "--key", required="-dl" not in sys.argv, help="Key or path to .key file")
   required_groups.add_argument("-r", "--repo", required=True, help="Repository name in the format of <user>/<project_name>")

   # Download
   download_group = parser.add_argument_group("Download")
   download_group.add_argument("-dl", "--download", help="Download the non-source code tag")
   download_group.add_argument("-o", "--output", help="Output directory for the download")

    
   # delete
   delete_group =parser.add_argument_group("Delete")
   delete_group.add_argument("-d", "--delete", help="Delete the following release name")
   delete_group.add_argument("-da", "--delete_all", help="Deletes all but the last release", action="store_true")

   # create`
   create_group = parser.add_argument_group("Create")
   create_group.add_argument("-c", "--create", help="Creates release with given name")
   create_group.add_argument("-m", "--message", required="-c" in sys.argv, help="Message of new release")

   # add an asset
   asset_group = parser.add_argument_group("Assets")
   asset_group.add_argument("-u", "--update", help="Update a release by adding another asset", action="store_true")
   asset_group.add_argument("-a", "--asset", required="-u" in sys.argv, help="Path to asset to upload")

   # optionals
   extras_group = parser.add_argument_group("Optional")
   extras_group.add_argument("-p", "--print", help="Print all release tags", action="store_true")
   extras_group.add_argument("-e", "--enterprise", help="Using enterprise Github account enter the address needed to connect to; provide a url like so: https://github.com/")
   extras_group.add_argument("-t", "--tag", required="-u" in sys.argv, help="Target tag")
   extras_group.add_argument("-gl", "--get_latest", help="Get latest tag", action="store_true")
   extras_group.add_argument("-v", help="Enable Versionings", action="store_true")

   args = parser.parse_args()
   return args 

def read_key(path_or_key):
    if _file_exist(path_or_key):
        path_or_key =os.path.abspath(path_or_key)
        with open(path_or_key, "r") as f:
            key = f.read()
            f.close()
        return key.strip() # key from path
    return path_or_key # key

def run(args):
    if args.download:
        gh = Github()
    else:
        key = read_key(args.key)
        gh = Github(base_url=args.enterprise + "/api/v3", login_or_token=key) if args.enterprise else Github(key)
    repository = gh.get_repo(args.repo)
    releases = repository.get_releases()

    if args.download:
        download_asset(args.output, args.download, releases)
        print("Download complete")

    if args.create:
        release, tag= create_release(repository, releases, args.create, args.message, args.tag)
        print("Created release: %s" % args.create)
        version = None
        if args.asset:
            if args.v:
                version = tag
        upload_asset(args.asset, release=release, version=version)
        print("Added asset to release: {}".format(version))

    if args.update:
        upload_asset(args.asset, tag=args.tag, releases=releases)
        print("Uploaded asset")
        
    if args.delete:
        [x.delete_release() for x in releases if x.title == args.delete or x.tag_name == args.delete]
        print("Deleted release: {0}".format(args.delete))

    if args.delete_all:
        delete_all(releases)
        print("Completed deleting releases")

    if args.print:
        [print("%s | %s" %(release.title, release.tag_name)) for release in releases]
    
    if args.get_latest:
        print(releases[0].tag_name) # 0 is always the last


if __name__ == "__main__":
    args = parse_cli()
    run(args)