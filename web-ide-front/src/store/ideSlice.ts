import { RootState } from './index'
import { createSlice } from '@reduxjs/toolkit'
import type { PayloadAction } from '@reduxjs/toolkit'
import { Tree, TreeNode } from '@/models/entry'

interface StateType {
  showExplorer: boolean
  showTerminal: boolean
  selectedNode: TreeNode | null
  currentFile: TreeNode | null
  tree: Tree | null
  showChatting: boolean
  fileExecuteResult: string
}

const initialState: StateType = {
  showExplorer: true,
  showTerminal: true,
  currentFile: null,
  selectedNode: null,
  tree: null,
  showChatting: false,
  fileExecuteResult: '',
}

export const ideSlice = createSlice({
  name: 'IDE',
  initialState,
  reducers: {
    toggleExplorer: state => {
      state.showExplorer = !state.showExplorer
    },
    toggleTerminal: state => {
      state.showTerminal = !state.showTerminal
    },
    toggleChatting: state => {
      state.showChatting = !state.showChatting
    },
    setSelectedNode: (state, action: PayloadAction<TreeNode>) => {
      state.selectedNode = action.payload
    },
    setCurrentFile: (state, action: PayloadAction<TreeNode>) => {
      state.currentFile = action.payload
    },
    setTree: (state, action: PayloadAction<Tree>) => {
      state.tree = action.payload
    },
    setFileExecuteResult: (state, action: PayloadAction<string>) => {
      state.fileExecuteResult = action.payload
    },
  },
})

export const {
  toggleExplorer,
  toggleTerminal,
  toggleChatting,
  setSelectedNode,
  setCurrentFile,
  setTree,
  setFileExecuteResult,
} = ideSlice.actions
export default ideSlice.reducer

export const selectShowExplorer = (state: RootState) => state.ide.showExplorer
export const selectShowTerminal = (state: RootState) => state.ide.showTerminal
export const selectSelectedNode = (state: RootState) => state.ide.selectedNode
export const selectShowChatting = (state: RootState) => state.ide.showChatting
export const selectTree = (state: RootState) => state.ide.tree
export const selectFileExecuteResult = (state: RootState) =>
  state.ide.fileExecuteResult
export const selectCurrentFile = (state: RootState) => state.ide.currentFile
