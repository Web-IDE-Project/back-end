import { Tree } from '@/models/entry'

const getExtension = (filename: string) => {
  return filename.slice(filename.lastIndexOf('.') + 1)
}

// Helper function to find node by ID
const findNodeById = (id: string, data: Tree) => {
  return data.find(node => node.id.toString() === id)
}

// Function to get the path of a node by ID
const getPathById = (id: string, data: Tree) => {
  const path = []
  let currentNode = findNodeById(id, data)

  if (!currentNode) {
    return null
  }

  // Traverse up the tree
  while (currentNode) {
    path.unshift(currentNode.name)
    if (currentNode.parent) {
      currentNode = findNodeById(currentNode.parent.toString(), data)
    } else {
      break
    }
  }

  return path.slice(1)
}

export { getExtension, getPathById }
