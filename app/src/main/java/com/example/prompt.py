import os
import sys

def print_tree(directory, prefix=''):
    """Recursively print the directory tree and file contents."""
    try:
        entries = os.listdir(directory)
        entries.sort()  # Sort entries alphabetically

        for i, entry in enumerate(entries):
            entry_path = os.path.join(directory, entry)
            is_last = (i == len(entries) - 1)

            if os.path.isdir(entry_path):
                print(prefix + "├── " + entry)
                new_prefix = prefix + ("│   " if not is_last else "    ")
                print_tree(entry_path, new_prefix)
            else:
                print(prefix + "├── " + entry)
                try:
                    with open(entry_path, 'r', encoding='utf-8') as file:
                        content = file.read()
                        print("\n////////////////////////////////////////////////////////////////////////////////////////")
                        print(content)
                        print("////////////////////////////////////////////////////////////////////////////////////////\n")
                except Exception as e:
                    print(f"Could not read file '{entry_path}': {e}")
    except Exception as e:
        print(f"An error occurred while accessing '{directory}': {e}")

if __name__ == "__main__":
    if len(sys.argv) != 2 :
        print("You must specify path")
    else:
        directory_path = sys.argv[1]
        if os.path.isdir(directory_path):
            print_tree(directory_path)
        else:
            print("The specified path is not a valid directory.")
