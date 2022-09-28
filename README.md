# Git Auto Prefix (IntelliJ Plugin)
Automatically set the jira issue key (of the current branch name) as prefix for the commit message

**Important:** This plugin only works on branches created by Jira (or manually, with exactly the same naming as jira would use)

## Installation
Plugin can be downloaded whithin IntelliJ or on the Jetbrains [Plugin Site](https://plugins.jetbrains.com/plugin/14238-git-auto-prefix)

## Features
* Automatically change the commit prefix if switching branches (also switching on cli/terminal is recognized) 
* If intellij is suggesting a (previous) commit message, only the prefix will be updated
* Set your custom delimiter between the issue key and the commit message (Default ": ")
* Wrap the issue key as you like

## Samples
Following prefix for commit messages will automatically be generated:

| Branch name                            	| Commit prefix (with delimiter) 	| Commit prefix (wrapped)
|----------------------------------------	|---------------	                |---------------
| main                                  	| no action     	                | no action
| master                                 	| no action     	                | no action
| bugfix/ABC-1234-app-not-working        	| ABC-1234:     	                | [ABC-1234]
| feature/ABC-1234-app-not-working       	| ABC-1234:     	                | [ABC-1234]
| release/ABC-1234-app-not-working       	| ABC-1234:     	                | [ABC-1234]
| someOtherType/ABC-1234-app-not-working 	| ABC-1234:     	                | [ABC-1234]
| ABC-1234-app-not-working               	| ABC-1234:     	                | [ABC-1234]
| ABC-1234                               	| ABC-1234:     	                | [ABC-1234]

