import subprocess
import os
import shlex
import click
import xml.etree.ElementTree as ET

class Deployer:
    def __init__(self, project_location, tag=False):
        self.project_location = project_location
        self.tag = tag
        self.version = self.read_version(os.path.join(project_location, "pom.xml"))

    def read_version(self, pom):
        tree = ET.parse(pom)
        root = tree.getroot()
        try:
            return root.findall("{http://maven.apache.org/POM/4.0.0}version")[0].text
        except IndexError:
            return None

    def create_tag(self):
        print("Tagging code release...")
        cmd = 'git push --delete origin %s' % (self.version,)
        subprocess.run(shlex.split(cmd), cwd=self.project_location)
        cmd = 'git tag --delete %s' % (self.version,)
        subprocess.run(shlex.split(cmd), cwd=self.project_location)

        cmd = 'git tag -a %s -m "%s"' % (self.version, self.version)
        subprocess.run(shlex.split(cmd), cwd=self.project_location)
        subprocess.run(shlex.split("git push origin --tags"), cwd=self.project_location)
        print("Tagged version %s" % self.version)

    def build(self):
        if self.version is None:
            print("No version found, check your pom.xml file")
            exit()

        if self.tag:
            self.create_tag()

        print("Building project...")
        cmd = "mvn clean install"
        subprocess.run(shlex.split(cmd), cwd=self.project_location)
        print("\n\nProject Built")


@click.command()
@click.argument("project_location", type=click.Path(exists=True))
@click.option("--real", is_flag=True)
def start_deployment(project_location, real):
    deployer = Deployer(project_location,  real)
    deployer.build()


if __name__ == "__main__":
    start_deployment()