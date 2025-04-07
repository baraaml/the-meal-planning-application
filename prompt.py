#!/usr/local/pyenv/shims/python3
import os
import sys
import argparse

# Exclude these directories entirely
EXCLUDED_DIRS = {'.git', 'node_modules', 'assets', 'venv'}

# Exclude these exact filenames
EXCLUDED_FILES = {'prompt.py', 'index.html', 'meals.csv'}

# Exclude files with these extensions
EXCLUDED_EXTENSIONS = {'.svg', '.js', '.csv', '.json'}

def should_exclude(entry_name, entry_path):
    """Return True if the file or folder should be excluded."""
    if entry_name in EXCLUDED_FILES:
        return True
    if os.path.isdir(entry_path) and entry_name in EXCLUDED_DIRS:
        return True
    if os.path.isfile(entry_path) and any(entry_name.endswith(ext) for ext in EXCLUDED_EXTENSIONS):
        return True
    return False

def print_tree(directory, prefix='', output_file=None):
    """Recursively print directory tree and include readable file contents."""
    try:
        entries = os.listdir(directory)
        entries.sort()

        # Filter out excluded items
        entries = [e for e in entries if not should_exclude(e, os.path.join(directory, e))]

        for i, entry in enumerate(entries):
            entry_path = os.path.join(directory, entry)
            is_last = (i == len(entries) - 1)
            connector = "└── " if is_last else "├── "

            if output_file:
                output_file.write(f"{prefix}{connector}{entry}\n")
            else:
                print(f"{prefix}{connector}{entry}")

            if os.path.isdir(entry_path):
                new_prefix = prefix + ("    " if is_last else "│   ")
                print_tree(entry_path, new_prefix, output_file)
            else:
                try:
                    with open(entry_path, 'r', encoding='utf-8') as file:
                        content = file.read()
                        separator = "//" * 40
                        if output_file:
                            output_file.write(f"\n{separator}\n")
                            output_file.write(content)
                            output_file.write(f"\n{separator}\n\n")
                        else:
                            print(f"\n{separator}")
                            print(content)
                            print(f"{separator}\n")
                except Exception as e:
                    error_msg = f"Could not read file '{entry_path}': {e}"
                    if output_file:
                        output_file.write(f"{error_msg}\n")
                    else:
                        print(error_msg)
    except Exception as e:
        error_msg = f"An error occurred while accessing '{directory}': {e}"
        if output_file:
            output_file.write(f"{error_msg}\n")
        else:
            print(error_msg)

def main():
    parser = argparse.ArgumentParser(description='Print directory tree while excluding certain folders and file types')
    parser.add_argument('input_dir', help='Input directory path')

    args = parser.parse_args()

    if not os.path.isdir(args.input_dir):
        print("Error: The specified input path is not a valid directory.")
        sys.exit(1)

    output_path = "chat.txt"
    try:
        with open(output_path, 'w', encoding='utf-8') as output_file:
            output_file.write(f"Directory Tree: {args.input_dir}\n")
            output_file.write("=" * 80 + "\n\n")
            print_tree(args.input_dir, output_file=output_file)
        print(f"Output written to {output_path}")
    except Exception as e:
        print(f"Error writing to output file: {e}")

if __name__ == "__main__":
    main()
